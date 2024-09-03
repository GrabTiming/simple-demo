package com.Lnn.util;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class ExportUtil {

    private static final int THREAD_NUM = 5;

    private static final int BUFFER_SIZE = 1024;

    private static final Charset charset = StandardCharsets.UTF_8;

    //导出失败重试次数
    private static final int RETRY_CNT = 3;

    /**
     * 导出本地任意文件
     */
    public static boolean exportFile(File file, HttpServletResponse response){

        response.setContentType("application/octet-stream;");
        response.setCharacterEncoding("UTF-8");
        try {
            response.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(file.getName(),"UTF-8") + "\"");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        long total = file.length();
        long chunkSize = (total + THREAD_NUM - 1)/THREAD_NUM;

        //每个线程处理出的结果需要放在一个线程安全的集合类
        List<Future<List<String>>> futureList = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_NUM);
        long start = 0;
        Map<Long ,Long> chunkMap = new HashMap<>();
        //分块处理
        for(int i=0;i<THREAD_NUM;i++)
        {
            chunkSize = padding(start,chunkSize,file);
            chunkMap.put(start,chunkSize);
            start+=chunkSize;
        }
        //分块处理后，怎么按原来的顺序放入？

//        for(Map.Entry<Long,Long> entry : chunkMap.entrySet()){
//            Future<List<String>> future = executor.submit(() -> handleChunk(file, entry.getKey(), entry.getValue()));
//            futureList.add(future);
//        }

        executor.shutdown();
        // 将结果按顺序写入响应输出流

        ReentrantLock lock = new ReentrantLock();

        for (Future<List<String>> future : futureList) {
            lock.lock();
            try {
                List<String> lines = future.get();

                for (String line : lines) {
                    if (!line.isEmpty()) {
                        log.info("line: {}", line);
                        // 使用 UTF-8 编码写入输出流
                        response.getOutputStream().write(line.getBytes(StandardCharsets.UTF_8));
                        response.getOutputStream().write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
                    }
                }
            } catch (ExecutionException | InterruptedException | IOException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }

        return true;
    }

    /**
     * 调整分块的长度，使每个分块结尾都是结束符
     * @param start 区块起点
     * @param chunkSize 区块长度
     * @param file 文件
     * @return 实际的区块长度
     */
    private static long padding(long start,long chunkSize,File file)
    {
        try(RandomAccessFile randomAccessFile = new RandomAccessFile(file,"r")){

            randomAccessFile.seek(start+chunkSize);
            boolean eol = false;
            switch (randomAccessFile.read()){
                case -1:
                case '\n':
                    eol = true;
                    break;
                case '\r':
                    eol = true;
                    break;
                default:
                    break;
            }
            if(!eol)
            {
                String readLine = randomAccessFile.readLine();
                chunkSize+=readLine.getBytes(charset).length;
                chunkSize+=1;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return chunkSize;
    }

    private static byte[] handleChunk(File file,long start,long chunkSize){

        int cnt = 0;
        byte[] result = new byte[(int)chunkSize];
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while (cnt < RETRY_CNT) {
            try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
                randomAccessFile.seek(start);
                int alreadyRead = 0 ;//已读字节数
                while(alreadyRead < chunkSize) {
                    int toRead = (int) Math.min(chunkSize - alreadyRead, BUFFER_SIZE);//将要读取的字节数
                    int readLen = randomAccessFile.read(buffer.array(),alreadyRead,toRead);
                    if (readLen == -1) {
                        break;
                    }
                    alreadyRead += readLen;
                    buffer.limit(readLen);
                    System.arraycopy(buffer.array(),0,result,alreadyRead,readLen);
                    buffer.clear();
                }
                return result;
            } catch (IOException e) {
                cnt++;
                System.out.println("出错文件路径: " + file.getPath() + " 起始位置: " + start);
                try {
                    Thread.sleep(100); // Sleep for some time before retrying
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt(); // 恢复中断状态
                }
            }
        }
        //这里需要记录出错块位置
        return null;
    }


}

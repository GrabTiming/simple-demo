package com.Lnn.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.mockito.internal.util.collections.ListUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
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
    public static boolean exportFile(File file, HttpServletResponse response) throws ExecutionException, InterruptedException {

        response.setContentType("application/octet-stream;");
        response.setCharacterEncoding("UTF-8");
        try {
            response.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(file.getName(),"UTF-8") + "\"");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        long fileSize = file.length();
        int chunkSize = 1024 * 1024 ; // 1MB
        int chunkCount = (int) Math.ceil((double) fileSize / chunkSize);

        ExecutorService executorService = Executors.newFixedThreadPool(4);
        List<Future<byte[]>> futures = new ArrayList<>();

        for (int i = 0; i < chunkCount; i++) {
            long start = i * chunkSize;
            long size = Math.min(chunkSize,fileSize-start);
            futures.add(executorService.submit(new ChunkReader(file, start, size)));
        }

        // 收集结果并确保顺序
        List<byte[]> resultChunks = new ArrayList<>(chunkCount);
        for (Future<byte[]> future : futures) {
            resultChunks.add(future.get());
        }

        // 合并结果
        byte[] finalResult = mergeChunks(resultChunks);
        executorService.shutdown();

        try {
            response.getOutputStream().write(finalResult);
        } catch (IOException e) {
            log.info("写入响应输出流出错");
            return false;
        }

        return true;
    }

    @Data
    @AllArgsConstructor
    private static class ChunkReader implements Callable<byte[]>{

        private File file;

        private long start;

        private long chunkSize;

        @Override
        public byte[] call() throws Exception {
            return handleChunk(file, start, chunkSize);
        }
    }

    private static byte[] mergeChunks(List<byte[]> chunks) {
        int totalLength = 0;
        for (byte[] chunk : chunks) {
            totalLength += chunk.length;
        }
        byte[] result = new byte[totalLength];
        int currentIndex = 0;
        for (byte[] chunk : chunks) {
            System.arraycopy(chunk, 0, result, currentIndex, chunk.length);
            currentIndex += chunk.length;
        }
        return result;
    }

    private static byte[] handleChunk(File file,long start,long chunkSize){

        int cnt = 0;
        byte[] result = new byte[(int)chunkSize];

        while (cnt < RETRY_CNT) {
            try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
                randomAccessFile.seek(start);
                randomAccessFile.read(result);
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

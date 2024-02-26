package com.antares.gsc.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.UUID;
import com.antares.gsc.common.enums.HttpCodeEnum;
import com.antares.gsc.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
public class FileUtils {
    public static char DOT = '.';

    /**
     * 上传文件并返回最终保存的路径
     * @param file 文件
     * @param targetDir 目标文件夹，如果不存在，会自动创建
     * @return 最终保存的路径
     */
    public static String saveFileToLocal(MultipartFile file, String targetDir){
        if (file.isEmpty()) {
            throw new BusinessException(HttpCodeEnum.BAD_REQUEST, "文件为空");
        }

        try {
            // 随机生成文件名
            String fileName = UUID.fastUUID().toString();
            //生成文件最终的路径
            String filePath = targetDir + File.separator + fileName + DOT + FileUtil.extName(file.getOriginalFilename());

            // 创建文件夹（如果不存在）
            FileUtil.mkdir(targetDir);

            InputStream inputStream = file.getInputStream();
            File destFile = new File(filePath);

            // 使用Hutool进行文件拷贝
            IoUtil.copy(inputStream, FileUtil.getOutputStream(destFile));
            IoUtil.close(inputStream);

            return filePath;
        } catch (IOException e) {
            log.error(e.toString());
            throw new BusinessException(HttpCodeEnum.INTERNAL_SERVER_ERROR, "文件上传失败");
        }
    }

    /**
     * 将fileList中的文件移动到targetDir下
     * @param filePaths
     * @param targetDir
     */
    public static void moveFilesToDirectory(List<String> filePaths, String targetDir) {
        // 创建目标目录（如果不存在）
        FileUtil.mkdir(targetDir);

        // 遍历文件列表，依次将文件移动到目标目录
        for (String filePath : filePaths) {
            FileUtil.move(new File(filePath), FileUtil.file(targetDir, FileUtil.getName(filePath)), true);
        }
    }

}

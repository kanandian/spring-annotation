package com.flyer.config;

import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import java.io.IOException;

public class MyTypeFilter implements TypeFilter {
    /**
     * 参数说明：
     * metadataReader：读取到当前正在扫描的类的信息
     * metadataReaderFactory：可以获取其他任何类信息的metadataReader
     * @param metadataReader
     * @param metadataReaderFactory
     * @return
     * @throws IOException
     */
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        // 获取当前类注解的信息
        AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
        // 获取当前类的类信息
        ClassMetadata classMetadata = metadataReader.getClassMetadata();
        // 获取当前类的资源信息（例如：类的路径）
        Resource resource = metadataReader.getResource();

        String className = classMetadata.getClassName();
        System.out.println(className);
        String[] classNames = className.split("[.]");
        className = classNames[classNames.length - 1];
        if (className.contains("er")) {
            return false;
        }
        return true;
    }
}

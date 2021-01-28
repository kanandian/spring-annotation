package com.flyer.condition;

import com.flyer.bean.Red;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;


// 自定义@Import需要注册的组件
public class MyImportSelector implements ImportSelector {

    /**
     * annotationMetadata: 当前标注@Import的类的所有注解信息
     * @param annotationMetadata
     * @return
     */

    // 返回值就是要导入到容器中的组件的全类名
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return new String[] {"com.flyer.bean.Red"};
    }
}

package com.home.framework.beans.support;

import com.home.framework.beans.LqdBeanWrapper;
import lombok.Data;

/**
 * @author liqingdong
 * @desc bean实例包装器顶级接口实现类, 可以包装代理类
 */
@Data
public class LqdBeanWrapperImpl implements LqdBeanWrapper {

    private Object wrappedInstance;

    public void setWrappedInstance(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
    }

    @Override
    public Object getWrappedInstance() {
        return this.wrappedInstance;
    }

    @Override
    public Class<?> getWrappedClass() {
        return this.wrappedInstance.getClass();
    }

}

package cn.hyrkg.fastspigot3.plugin.lifecycle;

import cn.hyrkg.fastspigot3.core.BeanManager;

public class LifecycleTestMain {

    public static void main(String[] args) {
        LifeRecorder.reset();
        BeanManager beanManager = new BeanManager();

        ComponentA a = new ComponentA();
        beanManager.wireInto(a);

        for (String e : LifeRecorder.events()) {
            System.out.println(e);
        }
    }
}



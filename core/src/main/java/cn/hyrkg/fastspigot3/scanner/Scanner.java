package cn.hyrkg.fastspigot3.scanner;

import java.util.List;

public interface Scanner {

    List<Class<?>> scan(String path);
}

package com.github.twogoods.jweave.agent;

import java.net.URL;
import java.net.URLClassLoader;

public class WeaveApplicationClassLoader extends URLClassLoader {

    public WeaveApplicationClassLoader(URL[] urls) {
        super(urls);
    }
}

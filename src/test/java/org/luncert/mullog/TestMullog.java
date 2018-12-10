package org.luncert.mullog;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class TestMullog {

    Mullog mullog = new Mullog("console1");

    @Test
    public void test() {
        mullog.info("desc", "msg", 1);
        mullog.setAppender("console2");
        mullog.info("hi");
    }

}
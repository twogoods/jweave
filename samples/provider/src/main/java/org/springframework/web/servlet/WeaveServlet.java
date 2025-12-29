package org.springframework.web.servlet;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author twogoods
 * @since 2024/11/28
 */
public class WeaveServlet {
    public DispatcherServlet dispatcherServlet;

    public WeaveServlet(DispatcherServlet dispatcherServlet) {
        this.dispatcherServlet = dispatcherServlet;
    }

    public String test() throws Exception {
//        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/echo");
//        request.addParameter("name","ttt");
//        MockHttpServletResponse response = new MockHttpServletResponse();
//        dispatcherServlet.doService(request, response);

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/post");
        request.addHeader("name", "ttt");
        request.setContentType("application/json");
        request.setContent("{\"name\":\"sad\",\"age\":1}".getBytes());
        MockHttpServletResponse response = new MockHttpServletResponse();
        dispatcherServlet.doService(request, response);
        String res = response.getContentAsString();
        System.out.println(response.getHeaderNames());
        return res;
    }

}

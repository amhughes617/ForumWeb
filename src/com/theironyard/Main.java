package com.theironyard;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    static HashMap<String, User> users = new HashMap<>();
    static ArrayList<Message> messages = new ArrayList<>();

    public static void main(String[] args) {
	// write your code here
        addTestUsers();  //adds default users for testing
        addTestMessages(); //adds default messages for testing

        Spark.init();
        Spark.get(
                "/",
                ((request, response) -> {
                    Session session = request.session();
                    String userName = session.attribute("userName");

                    String replyId = request.queryParams("replyId");
                    int replyIdNum = -1;
                    if (replyId != null) {
                        replyIdNum = Integer.valueOf(replyId);
                    }
                    HashMap m = new HashMap();
                    ArrayList<Message> threads = new ArrayList<>();
                    for (Message message : messages) {
                        if (message.replyId == replyIdNum) {
                            threads.add(message);
                        }
                    }
                    m.put("messages", threads);
                    m.put("userName", userName);
                    m.put("replyId", replyIdNum);
                    return new ModelAndView(m, "home.html");
                }),
                new MustacheTemplateEngine()
        );
        Spark.post(
                "/login",
                ((request, response) -> {
                    String userName = request.queryParams("loginName");
                    if (userName == null) {
                    }

                    User user = users.get(userName);
                    if (user == null) {
                        user = new User(userName, "");

                    }
                    Session session = request.session();
                    session.attribute("userName", userName);
                    response.redirect("/");
                    return "";
                })
        );
        Spark.post(
                "/logout",
                ((request, response) -> {
                    Session session = request.session();
                    session.invalidate();
                    response.redirect("/");
                    return "";
                })
        );
        Spark.post(
                "/create-message",
                ((request, response) -> {
                    Session session = request.session();
                    String userName = session.attribute("userName");
                    if (userName == null) {
                        throw new Exception("Not logged in");
                    }
                    String text = request.queryParams("messageText");
                    String replyId = request.queryParams("replyId");
                    if (text == null || replyId == null) {
                        throw new Exception("Did'nt get necessary parameters");
                    }
                    int replyIdNum = Integer.valueOf(replyId);
                    Message m = new Message(messages.size(), replyIdNum, userName, text);
                    messages.add(m);
                    response.redirect(request.headers("Referer"));
                    return "";
                })
        );
    }

    static void addTestUsers() {
        users.put("Alice", new User("Alice", ""));
        users.put("Bob", new User("Bob", ""));
        users.put("Charlie", new User("Charlie", ""));
    }

    static void addTestMessages() {
        messages.add(new Message(0, -1, "Alice", "Hello world!"));
        messages.add(new Message(1, -1, "Bob", "This is another thread!"));
        messages.add(new Message(2, 0, "Charlie", "Cool Thead, Alice."));
        messages.add(new Message(3, 2, "Alice", "Thanks"));
    }
}

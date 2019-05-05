package dev.bugakov.testapp;


import java.util.List;

class ItemQuestion {
    public long question_id;
    public String title;
}

public class StackApiResponse {
    public List<ItemQuestion> items;
    public boolean has_more;
}

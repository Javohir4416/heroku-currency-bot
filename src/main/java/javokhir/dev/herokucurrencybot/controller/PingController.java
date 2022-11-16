package javokhir.dev.herokucurrencybot.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@CrossOrigin(maxAge = 3600)
public class PingController {

    @GetMapping("/ping")
    public Category ping(){
        Category category=new Category();
        CategoryItem categoryItem1=new CategoryItem();
        categoryItem1.setCategoryId("54564");
        categoryItem1.setCategoryName("ald");
        CategoryItem categoryItem2=new CategoryItem();
        categoryItem2.setCategoryId("15621");
        categoryItem2.setCategoryName("hwdbjnakm");
        ArrayList<CategoryItem> categoryItemArrayList=new ArrayList<>();
        categoryItemArrayList.add(categoryItem1);
        categoryItemArrayList.add(categoryItem2);
        category.setCategory(categoryItemArrayList);
        return category;
    }
}

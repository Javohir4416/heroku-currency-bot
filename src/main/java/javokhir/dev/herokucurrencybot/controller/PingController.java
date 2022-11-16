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
        categoryItem1.setCategoryId("1");
        categoryItem1.setCategoryName("Birinchi");
        CategoryItem categoryItem2=new CategoryItem();
        categoryItem2.setCategoryId("2");
        categoryItem2.setCategoryName("Ikkinchi");
        CategoryItem categoryItem3=new CategoryItem();
        categoryItem3.setCategoryId("3");
        categoryItem3.setCategoryName("Uchinchi");
        CategoryItem categoryItem4=new CategoryItem();
        categoryItem4.setCategoryId("4");
        categoryItem4.setCategoryName("To'rtinchi");
        CategoryItem categoryItem5=new CategoryItem();
        categoryItem5.setCategoryId("5");
        categoryItem5.setCategoryName("Beshinchi");
        ArrayList<CategoryItem> categoryItemArrayList=new ArrayList<>();
        categoryItemArrayList.add(categoryItem1);
        categoryItemArrayList.add(categoryItem2);
        category.setCategory(categoryItemArrayList);
        return category;
    }
}

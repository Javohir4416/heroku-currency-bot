package javokhir.dev.herokucurrencybot.controller;

public class CategoryItem{
	private String categoryName;
	private String categoryId;

	public String getCategoryName(){
		return categoryName;
	}

	public String getCategoryId(){
		return categoryId;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
}

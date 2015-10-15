package com.pictitab.utils;

import java.util.List;

import com.pictitab.data.Category;

public class DataTools {

	/**
	 * Search a category.
	 * 
	 * @param categoryList
	 *            (ArrayList<Category): Categories list.
	 * @param name
	 *            (String): Name of the searched category.
	 * @return The category if it exists else null.
	 **/
	public static Category searchCategory(List<Category> categories, String name) {
		Category res = null;
		for (int i = 0; i < categories.size(); i++) {
			if (categories.get(i).getName().equals(name)) {
				res = categories.get(i);
				break;
			} else if (categories.get(i).getCategories().size() > 0) {
				res = searchCategoryIn(name, categories.get(i).getCategories());
				if (res != null)
					break;
			}
		}
		return res;
	}

	/**
	 * Search a category.
	 * 
	 * @param name
	 *            (String): Name of the searched category.
	 * @param categoryList
	 *            (ArrayList<Category): Categories list.
	 * @return The category if it exists else null.
	 **/
	private static Category searchCategoryIn(String name,
			List<Category> categoryList) {
		Category res = null;
		for (int i = 0; i < categoryList.size(); i++) {
			List<Category> subCategories = categoryList.get(i).getCategories();
			int size = 0;
			if (subCategories != null)
				size = subCategories.size();
			if (categoryList.get(i).getName().equals(name)) {
				res = categoryList.get(i);
				break;
			} else if (size > 0) {
				res = searchCategoryIn(name, categoryList.get(i)
						.getCategories());
				if (res != null)
					break;
			}
		}
		return res;
	}
}

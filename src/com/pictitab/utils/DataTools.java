package com.pictitab.utils;

import java.util.List;

import com.pictitab.data.Category;

public class DataTools {
	
	/**
	 * Methode recherchant une categorie avec le nom renseigne.
	 * @param name(String): Nom de la categorie recherchee.
	 * @return La categorie recherchee, si cette derniere est trouvee OU "null" sinon.
	 */
	public static Category searchCategory(List<Category> categories, String name) {
		Category res =null;
		for(int i=0;i<categories.size();i++) {
			if(categories.get(i).getName().equals(name)) {
				res =categories.get(i);
				break;
			}
			else if(categories.get(i).getCategories().size()>0) {
				res =searchCategoryIn(name, categories.get(i).getCategories());
				if(res != null)
					break;
			}
		}
		return res;
	}
	
	/**
	 * Methode permettant de chercher une categorie via son nom, dans une liste donnee.
	 * @param name(String): Nom de la categorie recherchee.
	 * @param categoryList(ArrayList<Category): La liste de categorie dans laquelle on souhaite faire la recherche.
	 * @return La categorie recherchee, si cette dernière est trouvee OU "null" sinon.
	 */
	private static Category searchCategoryIn(String name, List<Category> categoryList) {
		Category res =null;
		for(int i=0;i<categoryList.size();i++) {
			List<Category> subCategories = categoryList.get(i).getCategories();
			int size = 0;
			if(subCategories != null) size = subCategories.size();
			if(categoryList.get(i).getName().equals(name)) {
				res =categoryList.get(i);
				break;
			}
			else if(size > 0) {
				res =searchCategoryIn(name, categoryList.get(i).getCategories());
				if(res != null)
					break;
			}
		}
		return res;
	}
}

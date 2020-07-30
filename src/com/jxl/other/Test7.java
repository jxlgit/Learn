package com.jxl.other;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * stream
 */
public class Test7 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		List<String> listStr = new ArrayList<String>();
		listStr.add("111");
		listStr.add("222");
		
		listStr.stream().filter(new Predicate<String>() {
			@Override
			public boolean test(String t) {
				System.out.println(t);
				return false;
			}
		}).count();
		
		listStr.parallelStream().filter(new Predicate<String>() {

			@Override
			public boolean test(String t) {
				// TODO Auto-generated method stub
				return false;
			}
			
		}).count();
				

	}

}

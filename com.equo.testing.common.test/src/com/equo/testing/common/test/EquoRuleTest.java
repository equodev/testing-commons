package com.equo.testing.common.test;

import static org.junit.Assert.assertFalse;

import org.junit.Rule;
import org.junit.Test;

import com.equo.testing.common.util.EquoRule;

public class EquoRuleTest {
	
	@Rule
	public EquoRule rule = new EquoRule(this).runInNonUIThread(); 
	
	@Test
	public void ruleRunInNonUIThreadTest() {
		assertFalse(Thread.currentThread().getName().equals("main"));
	}
}

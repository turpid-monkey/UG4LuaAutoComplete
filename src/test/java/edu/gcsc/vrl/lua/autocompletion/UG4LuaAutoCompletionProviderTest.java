package edu.gcsc.vrl.lua.autocompletion;

import static org.junit.Assert.*;

import java.util.List;

import org.fife.ui.autocomplete.Completion;
import org.junit.Test;

public class UG4LuaAutoCompletionProviderTest {

	@Test
	public void testInit() {
		UG4LuaAutoCompletionProvider prov = new UG4LuaAutoCompletionProvider();
		List<Completion> l = prov.loadCompletions();
		assertEquals(4686, l.size());
	}

}

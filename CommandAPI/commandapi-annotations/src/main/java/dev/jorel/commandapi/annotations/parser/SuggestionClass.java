package dev.jorel.commandapi.annotations.parser;

import java.io.PrintWriter;

import javax.lang.model.element.TypeElement;

public class SuggestionClass implements Emittable {

	private final TypeElement typeElement;

	// Safe suggestions (SafeSuggestions<>) or normal suggestions (ArgumentSuggestions)?
	private final boolean isSafeSuggestions;

	// In the case of SafeSuggestions, the class that it's parameterized over
	private final String primitive;
	
	public SuggestionClass(TypeElement typeElement, boolean isSafeSuggestions, String primitive) {
		this.typeElement = typeElement;
		this.isSafeSuggestions = isSafeSuggestions;
		this.primitive = primitive;
	}

	@Override
	public int emit(PrintWriter out, int indentation) {
		// TODO: This emitter should be called from ArgumentData's emit method

		if(isSafeSuggestions) {
			// TODO: Semantics must check that whatever we're applying these suggestions to implements SafeOverrideableArgument.
			// TODO: Semantics must check that the type argument of SafeOverrideableArgument<?> matches this.primitive
			out.print(".replaceSafeSuggestions(new ");
		} else {
			out.print(".replaceSuggestions(new ");
		}
		
		// out.print(isSafeSuggestions)
		out.print("().get())");
		
//		out.println("// This class was automatically generated by the CommandAPI");
//		out.print("public class ");
//		out.print(commandClass.getSimpleName() + "$Command");
//		out.println(" {");
//		out.println();
//		indent++;
		
		return indentation;
	}
	
	

}
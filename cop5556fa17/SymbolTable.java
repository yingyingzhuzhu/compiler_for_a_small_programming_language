package cop5556fa17;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import cop5556fa17.AST.*;
import cop5556fa17.TypeUtils.Type;


public class SymbolTable {
	int current_scope;
	int next_scope;
	Stack<Integer> scope_stack = new Stack<>();
	HashMap<String,HashMap<Integer,Declaration>> table = new HashMap<>();


	public void enterScope(){
		current_scope = next_scope++;
		scope_stack.push(current_scope);
		
	}
	
	
	public void leaveScope(){
		current_scope = scope_stack.pop();
	}
	
	public boolean insert(String ident, Declaration dec){
		if(table.containsKey(ident)){// IDENTIFIER
			HashMap<Integer,Declaration> temp = table.get(ident);
			if(temp.containsKey(current_scope)){
				return false;
			}else{
				temp.put(current_scope, dec);
				table.put(ident, temp);
				return true;
			}
		}else{
			HashMap<Integer, Declaration> temp = new HashMap<>();
			temp.put(current_scope, dec);
			table.put(ident,temp);
			return true;
		}		
	}
	
	public Declaration lookupDec(String ident){
		if(!table.containsKey(ident)){
			return null;
		}
		HashMap<Integer,Declaration> temp = table.get(ident);
		int index = 0;
		for(int i = scope_stack.size() - 1;i > 0;i--){
			int scope = scope_stack.get(i);
			if(temp.get(scope) != null){
				index = i;
				break;
			}
		}
		return temp.get(index);
	}
	
	public Type lookupType(String ident){
		if(!table.containsKey(ident)){
			return null;
		}
		HashMap<Integer,Declaration> temp = table.get(ident);
		int index = 0;
		for(int i = scope_stack.size() - 1;i > 0;i--){
			int scope = scope_stack.get(i);
			if(temp.get(scope) != null){
				index = i;
				break;
			}
		}
		return temp.get(index).Type;
	}
		
	public SymbolTable() {
		int next_scope = 0;
		int current_scope = 0;
		HashMap<String, HashMap<Integer,Declaration>> table = new HashMap<>();
		Stack<Integer> scope_stack = new Stack<>();
		
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("The SymbolTable is :");
		sb.append('\n');
		Set<String> tableset = table.keySet();
		Iterator<String> it = tableset.iterator();
		while(it.hasNext()){
			String ident = it.next();
			sb.append("Ident is" + ident  + "; Scope number is");
			Map<Integer,Declaration> scopemap = table.get(ident);
			Set<Integer> decscope = scopemap.keySet();
			Iterator<Integer> integerit = decscope.iterator();
			while(integerit.hasNext()){
				int scopenumber = integerit.next();
				sb.append(scopenumber + ",");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
}

import java.lang.reflect.Method;


public class State {
	private String stateName;
	
	public Method onEntry;
	public Method onExit;
	public Method nextState;
	
	public String getName(){
		return this.stateName;
	}
	
	public void setName(String name){
		this.stateName = name;
	}
	
	
	State(String name){
		this.stateName = name;
	}
	
	public void addEntryAction(Method entry){
		this.onEntry = entry;
	}
	
	public void addExitAction(Method exit){
		this.onExit = exit;
	}
	
	public void addTransitions(Method transitions){
		this.nextState = transitions;
	}
}

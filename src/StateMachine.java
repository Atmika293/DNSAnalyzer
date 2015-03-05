
import java.util.Observable;


public class StateMachine extends Observable  {

	private State currentState;
	private State initialState;
	private State endState;
	private State[] states;
	private int numofStates;
	
	StateMachine(int nost){
		char name = 'A';
		this.initialState = new State("Initial");
		this.endState = new State("End");
		this.numofStates = nost;
		this.states = new State[this.numofStates];
		for(int i = 0;i < this.numofStates;i++){
			this.states[i] = new State(Character.toString((char)(name + i)));
		}
		this.currentState = this.initialState;
	}
	
	public void setCurrentState(TransitionSupport t){
		this.currentState = t.getEndState();
		setChanged();
		notifyObservers(t);
	}
	
	public State getInitialState(){
		return this.initialState;
	}
	
	public State getEndState(){
		return this.endState;
	}
	
	public int getNumberofStates(){
		return this.numofStates;
	}
	
	public State getState(String name){
		for(State st : this.states){
			if(st.getName().equalsIgnoreCase(name))
				return st;
		}
		System.out.println("State not found!");
		return null;
	}
	
	public State getCurrentState(){
		return this.currentState;
	}
	
}
	

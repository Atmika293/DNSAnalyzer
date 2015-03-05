import java.lang.reflect.InvocationTargetException;
import java.util.Observable;
import java.util.Observer;


public class Transition implements Observer {
	private static State begin,end;
	
	public State getfromState(){
		return begin;
	}
	
	public State gettoState(){
		return end;
	}
	
	public void update(Observable o, Object obj) {
		// TODO Auto-generated method stub
		TransitionSupport input = (TransitionSupport) obj;
		begin = input.getStartState();
		end = input.getEndState();
		try {
			if(input.getArguments() == null)
				begin.onExit.invoke(input.getCallingObject(),(Object[])null);
			else
				begin.onExit.invoke(input.getCallingObject(),input.getfromArguments());
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			if(input.getArguments() == null)
				end.onEntry.invoke(input.getCallingObject(),(Object[])null);
			else
				end.onEntry.invoke(input.getCallingObject(),input.gettoArguments());
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

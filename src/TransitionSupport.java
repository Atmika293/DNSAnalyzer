import java.util.Arrays;


public class TransitionSupport {
		private State from,to;
		private Object caller;
		private Object[] arguments;
		private int exitparams,entryparams;
		
		TransitionSupport(State b,State e){
			this.from = b;
			this.to = e;
			this.caller = new Activity();
			this.arguments = null;
		}
		
		TransitionSupport(State b,State e,Object callerObj,int exp,int enp,Object...parameters){
			this.from = b;
			this.to = e;
			this.caller = callerObj;
			this.arguments = parameters;
			this.entryparams = enp;
			this.exitparams = exp;
		}
		
		public void setArguments(int exp,int enp,Object...parameters){
			this.arguments = parameters;
			this.entryparams = enp;
			this.exitparams = exp;
		}
		
		
		public State getStartState(){
			return this.from;
		}
		
		public State getEndState(){
			return this.to;
		}
		
		public Object getCallingObject(){
			return this.caller;
		}
		
		public Object[] getfromArguments(){
			return Arrays.copyOfRange(this.arguments,0,this.entryparams);
		}
		
		public Object[] gettoArguments(){
			return Arrays.copyOfRange(this.arguments,this.entryparams,this.entryparams + this.exitparams);
		}
		
		public Object[] getArguments(){
			return this.arguments;
		}
}

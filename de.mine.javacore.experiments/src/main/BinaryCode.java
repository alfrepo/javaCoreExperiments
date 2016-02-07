
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class BinaryCode{
	
	public static void main(String[] args){
		BinaryCode b = new BinaryCode();
		b.decode("12221112222221112221111111112221111");
	}

	public String[] decode(String message){
		String second =  decodeOneString(message,1);
		String first =  decodeOneString(message, 0);
		
		printResult(message, first, second);
		
		return new String[]{first,second};
	}
	
	private String decodeOneString(String message, int first){
		List<Integer> codelist = convert(message);
		
		Integer[] decodedArray = new Integer[codelist.size()];
		decodedArray[0] =  first;
		
		ArrayList<Integer> decodedList =new ArrayList<Integer>(); 
		Collections.addAll(decodedList, decodedArray);
		
		
		//codenum in {0,1,2,3,}
		ListIterator<Integer> codelistIter = codelist.listIterator();
		while(codelistIter.hasNext()){
			int i = codelistIter.nextIndex();
			int cnt = codelistIter.next();
			
			decodedList = distribute(cnt, i, decodedList);
			if(decodedList == null){
				return "NONE";
			}
			print( codelist, i, decodedList);
		}
		
		return merge(decodedList);
	}
	
	// pos in {3,2,1}
	private ArrayList<Integer> distribute(int cnt, int i, ArrayList<Integer> decoded ){
		
		for(int j=i-1,distSpace=3; j<=i+1; j++,distSpace-- ){
			
			//cnt fits?
			if(distSpace < cnt){
				return null;
			}
			
			//border
			if(j<0) continue;
			if(j>= decoded.size()) continue;
			
			//exists?
			if(decoded.get(j) != null){
				cnt-= decoded.get(j);
				//not enought cnt?
				if(cnt<0){
					return null;
				}
				continue;
			}
			
			//ambigous?
			if( cnt>0 && distSpace>cnt) return null;
			
			decoded.set(j, pop(cnt));
		}
		
		return decoded;
	}
	
	private String merge(Collection<Integer> collection){
		StringBuffer strb = new StringBuffer();
		for(Integer num:collection){
			strb.append(num);
		}
		return strb.toString();
	}
	
	
	private List<Integer> convert(String string){
		String[] singleLetters = string.split("");
		
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(int i=1; i<singleLetters.length; i++){
			//TODO: catch parse error, return null
			int number = Integer.parseInt(singleLetters[i]);
			list.add(number);
		}
		return list;
	}
	
	
	private Integer pop(Integer count){
		if(count==0){
			return 0;
		}
		count--;
		return 1;
	}
	
	
	private void print(List<Integer> codes, int i, List<Integer> decoded){
		System.out.print("0: ");
		for(Integer c:codes){
			System.out.print(c);
		}
		System.out.print("\n");
		System.out.print("c: ");
		for(Integer c:codes.subList(0, i+1)){
			if(c!= null)
			System.out.print(c);
		}
		System.out.print("\n");
		System.out.print("d: ");
		for(Integer d:decoded){
			if(d!= null)
			System.out.print(d);
		}
		System.out.print("\n\n");
	}
	
	private void printResult(String encoded, String first, String second ){
		System.out.println("Enc   : "+encoded);
		System.out.println("First : "+first);
		System.out.println("Second: "+second);
	}


}

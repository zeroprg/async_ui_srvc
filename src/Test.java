public class Test {
	public static void main(String[] args) {
   Test test = new Test();
   String result = test.reverseString("1234567890");
   System.out.println("result := " + result);
	}

public String reverseString(String input) 
{
String ret =  null;
    if(input != null && !input.isEmpty()) 
    {
   //     char array[] = input.toCharArray();
        int n = input.length();
         StringBuilder sbOutput = new StringBuilder(n);
        for(int i= 0; i < n; i++ ) 
        {
        //	System.out.print//(array[n-i-1]);
               sbOutput.append( input.charAt(n-i-1));
            
        }
       ret = sbOutput .toString();
   } 
   else {
       if(input != null ) ret = input;
   }
  return ret;
}

}

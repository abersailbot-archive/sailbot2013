package routeplanner.datasource.gpsd;

public class GPSTokenizer
{
    String data;
    int offset=0;
    
    public GPSTokenizer(String data)
    {
         this.data = data;
    }
    
    /**
     * grabs the next token
     * returns null if its blank
     * @return
     */
    public String getNextToken()
    {
        int nextComma = data.indexOf(',', offset);
        
        //handle the last item in the string
        if(nextComma<0)
        {
            return data.substring(offset);
        }

        //handle blank vlaues e.g. where we have ,, 
        if(nextComma==offset)
        {
            //there is no data between the tokens
            offset=nextComma+1;
            return null;
        }
        //handle all other situations
        String token = data.substring(offset,nextComma);
        offset=nextComma+1;
        return token;
    }
}

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.net.Socket;

public class TwitchInputBot
{
    public static void main(String[] args) throws Exception
    {
        // The server to connect to and our details.
        final String server = "irc.twitch.tv";
        final String nick = "twitchplaysmelee";
        final String oauth = "oauth:ibmaxxvbys7l4vq5d3zbfalhxceudp";
        ArrayList<String> inputList = new ArrayList<String>();
        ArrayList<String> possibleInputs = new ArrayList<String>();
        HashMap<String, Integer> inputCount = new HashMap<String, Integer>();

        possibleInputs.add("a");
        possibleInputs.add("b");
        possibleInputs.add("x");
        possibleInputs.add("y");
        possibleInputs.add("z");
        possibleInputs.add("l");
        possibleInputs.add("r");
        possibleInputs.add("stickup");
        possibleInputs.add("stickupright");
        possibleInputs.add("stickright");
        possibleInputs.add("stickdownright");
		possibleInputs.add("stickdown");
        possibleInputs.add("stickdownleft");
        possibleInputs.add("stickleft");
		possibleInputs.add("stickupleft");
        possibleInputs.add("cstickup");
		possibleInputs.add("cstickupright");
		possibleInputs.add("cstickright");
		possibleInputs.add("ctickdownright");
		possibleInputs.add("cstickdown");
		possibleInputs.add("cstickdownleft");
		possibleInputs.add("cstickleft");
		possibleInputs.add("cstickupleft");
        possibleInputs.add("dpadup");
        possibleInputs.add("dpadupright");
        possibleInputs.add("dpadright");
        possibleInputs.add("dpaddownright");
        possibleInputs.add("dpaddown");
        possibleInputs.add("dpaddownleft");
        possibleInputs.add("dpadleft");
        possibleInputs.add("dpadupleft");
        possibleInputs.add("start");

        // The channel which the bot will join.
        String channel = "#twitchplaysmelee";

        // Connect directly to the IRC server.
        Socket socket = new Socket(server, 6667);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Log on to the server.
        writer.write("PASS " + oauth + "\r\n");
        writer.write("NICK " + nick + "\r\n");
        writer.flush( );

        // Read lines from the server until it tells us we have connected.
        String line = null;
        while ((line = reader.readLine( )) != null)
        {
            if (line.indexOf("004") >= 0)
            {
                // We are now logged in.
                break;
            }
            else if (line.indexOf("433") >= 0)
            {
                System.out.println("Nickname is already in use.");
                return;
            }
        }

        // Join the channel.
        writer.write("JOIN " + channel + "\r\n");
        writer.flush( );

        // Keep reading lines from the server.
        while ((line = reader.readLine( )) != null)
        {
            if (line.toLowerCase( ).startsWith("PING "))
            {
                // We must respond to PINGs to avoid being disconnected.
                writer.write("PONG " + line.substring(5) + "\r\n");
                writer.write("PRIVMSG " + channel + " :I got pinged!\r\n");
                writer.flush();
            }
            else
            {
                // Print the raw line received by the bot.
                String input = line;
				int index = input.indexOf(" :");
				if(index != -1)
				{
					input = input.substring(index+2, input.length());
					input = input.toLowerCase();
					StringTokenizer tokenizer = new StringTokenizer(input);
					ArrayList<String> currentTokens = new ArrayList<String>();
					while(tokenizer.hasMoreTokens())
					{
						String token = tokenizer.nextToken();
						if(possibleInputs.contains(token))
						{
							currentTokens.add(token);
						}
					}
					if(!currentTokens.isEmpty())
					{
						currentTokens.sort(new Comparator<String>()
						{
							public int compare(String string1, String string2)
							{
								return string1.compareTo(string2);
							}
						});
						String output = "";
						for(int i = 0; i<currentTokens.size();i++)
						{
							output += currentTokens.get(i) + " ";
						}

						if(inputCount.get(output) != null)
							inputCount.put(output, new Integer(inputCount.get(output) + 1));
						else
							inputCount.put(output, new Integer(1));

						System.out.println(inputCount.get(output).intValue());
					}
				}
				System.out.println(line);
            }
        }
    }

}
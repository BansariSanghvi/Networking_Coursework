// IN2011 Computer Networks
// Coursework 2023/2024
//
// Submission by
// YOUR_NAME_GOES_HERE
// YOUR_STUDENT_ID_NUMBER_GOES_HERE
// YOUR_EMAIL_GOES_HERE


import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

// DO NOT EDIT starts
interface TemporaryNodeInterface {
    public boolean start(String startingNodeName, String startingNodeAddress);
    public boolean store(String key, String value);
    public String get(String key);
}
// DO NOT EDIT ends


public class TemporaryNode implements TemporaryNodeInterface {

    private String nodeName;
    private HashMap<String, String> nodeHashTable = new HashMap<>();

    public boolean start(String startingNodeName, String startingNodeAddress) {
	// Implement this!
	// Return true if the 2D#4 network can be contacted
	// Return false if the 2D#4 network can't be contacted

        if(startingNodeName != null && startingNodeAddress != null) {

            // Split the startingNodeAddress into IP address and port number
            String[] addressParts = startingNodeAddress.split(":");
            if (addressParts.length != 2) {
                // Invalid address format
                return false;
            }
            String ipAddress = addressParts[0];
            String port = addressParts[1];

            // Attempt to connect to the network
            boolean connectionStatus = connectToNetwork(ipAddress, port);

            // Return the connection status
            return connectionStatus;
        }

        //No Starting Node Name or Starting Node Address was provided.
        return false;


    }

    public boolean store(String key, String value) {
	// Return true if the store worked , Return false if the store failed
    // Calculate the HashID of a specific name. -> method in HashID that has computeHashID function.
    // Check if the connection is successful. If the connection is successful, then add to the map.
        try{

            // Compute HashID based on node name.
            byte[] hashBytes = HashID.computeHashID(key + "\n");
            String hashID = new String(hashBytes, StandardCharsets.UTF_8);

            // Add the key-value pair to the hash map
            nodeHashTable.put(nodeName,hashID);

            return true; // Indicate successful storage
        } catch (Exception e) {
            // Handle hash computation exception
            e.printStackTrace();
            return false;

        }
    }

    public String get(String key) {
	// Return the string if the get worked, Return null if it didn't
    // Compute HashID of the key.
    // Check if it has a value stored for that Key
        try {
            // Split the key request to get the number of lines and the key
            String[] requestParts = key.split("\n");

            // Extract the number of lines and the key
            String firstLine = requestParts[0];
            String[] firstLineParts = firstLine.split(" ");

            int numLines = Integer.parseInt(firstLineParts[1]);

            // Construct the key from subsequent lines
            StringBuilder keyBuilder = new StringBuilder();

            for (int i = 1; i <= numLines; i++) {
                keyBuilder.append(requestParts[i]);
                if (i < numLines) {
                    keyBuilder.append("\n");
                }
            }
            String keyHash = keyBuilder.toString();

            // Compute the hash ID of the key
            byte[] hashBytes = HashID.computeHashID(keyHash + "\n");
            String hashID = bytesToHex(hashBytes);

            // Check if the hash ID exists in the hash table
            if (nodeHashTable.containsKey(hashID)) {
                String value = nodeHashTable.get(hashID);
                // Prepare and return the VALUE response
                return hashID;

            } else {
                // Respond with NOPE if the key is not found
                return "NOPE";
            }
        } catch (Exception e) {
            // Handle any exceptions
            e.printStackTrace();
            return "Error processing GET request";
        }
    }




    private boolean connectToNetwork(String ipAddress, String port) {
        try {
            // Create a socket to connect to the specified IP address and port
            Socket requester = new Socket(ipAddress, Integer.parseInt(port));

            BufferedReader reader = new BufferedReader(new InputStreamReader(requester.getInputStream()));
            Writer writer = new OutputStreamWriter(requester.getOutputStream());

            // Send a message to the Network.
            writer.write("Sending Message from TempNode\n");
            writer.flush();

            //Check if we received a message from a Full Node.
            String response = reader.readLine();
            System.out.println("A Full Node said : " + response);

            // Close the socket after establishing connection
            requester.close();

            // Connection successful
            return true;

        } catch (IOException e) {
            // Connection failed
            return false;
        }
    }

    private String bytesToHex(byte[] hashBytes) {
        StringBuilder hexString = new StringBuilder(2 * hashBytes.length);
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

}

import java.util.*;

public class FirstFitAlgorithm {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        Scanner input2 = new Scanner(System.in);
        int ramSize = input.nextInt();
        List<String> processList = new ArrayList<>();
        List<Integer> ramSpaceList = new ArrayList<>();
        List runList = new ArrayList<>();
        int processSpace = 0 , diff = 0 , extant = 0;
        String name = "";
        while (input2.hasNextLine()) {
            String text = input2.nextLine().trim();
            String[] strings = text.split(" ");
            processList.add(strings[1]);
            if (strings.length == 2){
                ramSpaceList.add(0);
            }else {
                ramSpaceList.add(Integer.valueOf(strings[2]));
            }
        }
        for (Integer integer : ramSpaceList) {
            if (integer == 0){
                processSpace = ramSpaceList.get(integer);
                int index = ramSpaceList.indexOf(integer);
                String processName = processList.get(index);
                processList.remove(index);
                if (processList.contains(processName)){
                    int indexOf = processList.indexOf(processName);
                    processList.remove(indexOf);
                    ramSpaceList.remove(indexOf);
                    if (integer <= processSpace){
                        String processName2 = processList.get(integer);
                        runList.add(processName2);
                        diff = processSpace - integer;
                        processSpace = diff;
                    }
                }
            }

            if (diff > 0){
                runList.add(diff);
            }
        }
        extant = ramSize - diff;
        if (extant != 0){
            runList.add(extant);
        }

        System.out.println(runList);

        input2.close();

    }
    public static Map<String,Integer> getMap(String text , Map<String,Integer> map){
        String[] strings = text.split(" ");
        List<String> list = new ArrayList<>();
        if (strings.length > 2) {
            map.put(strings[1], Integer.valueOf(strings[2]));
        }
        return map;
    }
    public static List<String> getList(String text , List<String> list){
        String[] strings = text.split(" ");
         if (strings.length == 2){
            list.add(strings[1]);
        }
        return list;
    }
}

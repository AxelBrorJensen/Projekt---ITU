import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

import itumulator.world.*;
import itumulator.*;

public class InputParser {

    private Random r = new Random();

    public void loadFromFile(String filename, World world) throws FileNotFoundException {

        File file = new File(filename);
        System.out.println("Indlæser fil: " + file.getAbsolutePath()); // Debug

        Scanner sc = new Scanner(file);

        int size = Integer.parseInt(sc.nextLine().trim());

        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split(" ");
            String type = parts[0];
            String count = parts[1];

            int amount;

            if (count.contains("-")) {
                String[] p = count.split("-");
                int min = Integer.parseInt(p[0]);
                int max = Integer.parseInt(p[1]);
                amount = r.nextInt(max - min + 1) + min;
            } else {
                amount = Integer.parseInt(count);
            }

            if (type.equals("grass")) {
                placeGrass(world, amount);
            } else if (type.equals("rabbit")) {
                placeRabbits(world, amount);
            }

        }
    }

    // --- PLACE GRASS ---
    private void placeGrass(World world, int amount) {
        for (int i = 0; i < amount; i++) {
            Location l = getRandomLocationAllowNonblocking(world);
            world.setTile(l, new Grass());
        }
    }
    
    // --- PLACE RABBITS ---
    private void placeRabbits(World world, int amount) {
        for (int i = 0; i < amount; i++) {
            Location l = getRandomLocationBlockingOnly(world);
            world.setTile(l, new Rabbit());
        }
    }

    private Location getRandomLocationBlockingOnly(World world) {
        int size = world.getSize();
        while (true) {
            int x = r.nextInt(size);
            int y = r.nextInt(size);
            Location l = new Location(x, y);

            if (world.isTileEmpty(l)) return l;
        }
    }

    private Location getRandomLocationAllowNonblocking(World world) {
        int size = world.getSize();
        while (true) {
            int x = r.nextInt(size);
            int y = r.nextInt(size);
            Location l = new Location(x, y);

            // Grass må gerne ligge ovenpå andre non-blocking
            if (world.isTileEmpty(l) || !world.containsNonBlocking(l)) return l;
        }
    }
}

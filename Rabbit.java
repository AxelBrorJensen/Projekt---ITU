package itumulator.simulator;

import itumulator.simulator.Actor;
import itumulator.world.World;
import itumulator.world.Location;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
public class Rabbit implements Actor {
    private int energy = 20; 
    private Random rand = new Random();
    private int age = 0;
    private int maxEnergy = 20;
    
    private Burrow OwnBurrow = null;
    private boolean isHiddenInBurrow = false;

    
     @Override
    public void act(World world) {
        //Nat - kaninen bliver fjernet om natten
        if (world.isNight()) {
            // Hvis kaninen har sit eget hul
            if (OwnBurrow != null) {
                Location myLoc = world.getLocation(this);
                Location burrowLoc = world.getLocation(OwnBurrow);
        
                // Hvis kaninen IKKE står på hullet går mod det
                if (myLoc != null && !myLoc.equals(burrowLoc)) {
        
                    int dx = Integer.compare(burrowLoc.getX(), myLoc.getX());
                    int dy = Integer.compare(burrowLoc.getY(), myLoc.getY());
        
                    Location next = new Location(myLoc.getX() + dx, myLoc.getY() + dy);
        
                    // Bevæger sig hvis der ikke er et hul
                    if (world.isTileEmpty(next)) {
                        world.move(this, next);
                        return; 
                    }
                    return;
                }
        
                // Hvis kaninen står PÅ hullet → sov
                if (myLoc != null && myLoc.equals(burrowLoc)) {
                    if (!isHiddenInBurrow) {
                        isHiddenInBurrow = true;
                        world.remove(this);
                    }
                }
            }
            return; // Kaninen gør intet andet om natten
        }


        // Dag - kaninen kommer fra igen fra sit hul
        if (!world.isNight() && isHiddenInBurrow) {
            isHiddenInBurrow = false;

            Location burrowPos = world.getLocation(OwnBurrow);
            if (burrowPos != null) {
                world.setTile(burrowPos, this);
            }
        }
        
        //Alder stiger
        age++;
        
        // Rabbits mister 1 energi per tic
        energy--;
        
        // maxEnergy falder når kaninen bliver ældre
        if (age % 10 == 0) {
            maxEnergy--;
        }
        
        //Energy kan ikke overstige maxEnergy
        if (energy > maxEnergy) {
            energy = maxEnergy;
        }
        
        //Dødsbetingelse 
        if (maxEnergy <= 0 || energy <= 0) {
            world.delete(this);
            return;
        }
        
        //finder placering
        Location myPos = world.getLocation(this);
        
        //Spiser græs på samme felt og får energy
        Object grass = world.getNonBlocking(myPos);
        if (grass != null) {
            world.remove(grass);        
            energy += 5;
        }
    
        //Reproducere
        if (age >= 5 && rand.nextDouble() < 0.2) { //ældre end 5 og sandsynlighed 20%
            myPos = world.getLocation(this);
            Set<Location> Emptyneighbours = world.getEmptySurroundingTiles(myPos);
            if (!Emptyneighbours.isEmpty()) {
            List<Location> list = new ArrayList<>(Emptyneighbours);
            Location babyLocation = list.get(rand.nextInt(list.size()));
            world.setTile(babyLocation, new Rabbit());
            }
        }
        
        //Bevægelse
        myPos = world.getLocation(this);
        Set<Location> neighbours = world.getEmptySurroundingTiles(myPos);
        if (!neighbours.isEmpty()) {
            List<Location> list = new ArrayList<>(neighbours);
            Location randomLocation = list.get(rand.nextInt(list.size()));
            world.move(this, randomLocation);
        }
                
        
        // kan grave huller
        if (OwnBurrow == null && age >= 1 && rand.nextDouble() < 0.05) {
        
            //Find lokation
            myPos = world.getLocation(this);
        
            //Tjek for huller i nærheden 
            List<Location> burrowsNearby = new ArrayList<>();
        
            for (Location loc : world.getSurroundingTiles(myPos)) {
                Object nb = world.getNonBlocking(loc); 
                if (nb != null && nb.getClass() == Burrow.class) {
                    burrowsNearby.add(loc);
                }
            }
            // Kaninerne kan dele deres huller
            if (!burrowsNearby.isEmpty()) {
                OwnBurrow = (Burrow) world.getNonBlocking(burrowsNearby.get(0));
            }
        
            // Graver det hul hvis der er ikkke er et lige ved siden af
            else {
                Set<Location> empty = world.getEmptySurroundingTiles(myPos);
                if (!empty.isEmpty()) {
                    List<Location> list = new ArrayList<>(empty);
                    Location burrowPos = list.get(rand.nextInt(list.size()));
        
                    Burrow b = new Burrow();
                    world.setTile(burrowPos, b);
                    OwnBurrow = b;
                }
            }
        }
    }
}

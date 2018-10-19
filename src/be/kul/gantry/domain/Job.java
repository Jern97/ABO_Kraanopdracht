package be.kul.gantry.domain;

/**
 * Created by Wim on 12/05/2015.
 */
public class Job {

    private final int id;

    private final Task pickup;
    private final Task place;

    private final Item item;


    public Job(int id, Item c, Slot from, Slot to) {
        this.id = id;
        this.item = c;
        this.pickup = new Task(id*2,TaskType.PICKUP);
        this.place = new Task(id*2+1,TaskType.PLACE);
        this.pickup.slot = from;
        this.place.slot = to;
    }

    public int getId() {
        return id;
    }

    public Task getPickup() {
        return pickup;
    }

    public Task getPlace() {
        return place;
    }

    public Item getItem() {
        return item;
    }

    @Override
    public String toString() {
        return String.format("J%d move %d from %s to %s",id,item.getId(),pickup.slot,place.slot);
    }

    public class Task {
        private final int id;
        private Slot slot;
        private Job parentJob;
        private TaskType type;

        public Task(int id, TaskType taskType) {
            this.id = id;
            this.type = taskType;
            this.parentJob = Job.this;
        }

        public int getId() {
            return id;
        }

        public Slot getSlot() {
            return slot;
        }

        public void setSlot(Slot slot) {
            this.slot = slot;
        }

        public Job getParentJob() {
            return parentJob;
        }

        public TaskType getType() {
            return type;
        }

        @Override
        public String toString() {
            if(type == TaskType.PICKUP) {
                return String.format("Pickup %d from %s",Job.this.item.getId(),slot);
            } else {
                return String.format("Place %d at %s",Job.this.item.getId(),slot);
            }
        }
    }

    public static enum TaskType {
        PICKUP,
        PLACE
    }

}

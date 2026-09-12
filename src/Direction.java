public enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    public static Direction random() {
        Direction[] directions = values();
        return directions[RandomUtils.RANDOM.nextInt(directions.length)];
    }
}

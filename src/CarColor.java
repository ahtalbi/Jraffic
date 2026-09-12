public enum CarColor {
    RED,
    GREEN,
    BLUE;

    public static CarColor random() {
        CarColor[] colors = values();
        return colors[RandomUtils.RANDOM.nextInt(colors.length)];
    }
}

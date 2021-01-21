public class Params {
    String raw;
    String x;
    String y;
    String imgName;

    public Params(String x, String y, String imgName) {
        this.x = x;
        this.y = y;
        this.imgName = imgName;
        this.raw = x + "##" + y + "##" + imgName;
    }

    public Params(String x, String y) {
        this.x = x;
        this.y = y;
    }

    public String toRawString() {
        return x + "##" + y + "##" + imgName;
    }

    public String getImgName() {
        return this.imgName;
    }

    public void setXY(String x, String y) {
        this.x = x;
        this.y = y;
    }

    public void setImgName(String name) {
        this.imgName = name;
    }

    public Integer getX() {
        return Integer.parseInt(this.x);
    }

    public Integer getY() {
        return Integer.parseInt(this.y);
    }

    public static Params fromRawString(String rawString) {
        String[] parts = rawString.split("##");
        String x = parts[0];
        String y = parts[1];
        String imgName = parts[2];

        return new Params(x, y, imgName);
    }
}
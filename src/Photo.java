import java.util.Set;

public class Photo {

    public int number;
    public int tagCount;
    public Set<String> tags;
    boolean isVert;

    public Photo(int number, int tagCount, Set<String> tags, boolean isVert) {
        this.number = number;
        this.tagCount = tagCount;
        this.tags = tags;
        this.isVert = isVert;
    }
}

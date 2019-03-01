import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    static ArrayList<int[]> result = new ArrayList<>();
    static HashMap<String, List<Integer>> tags;

    //a_example.txt b_lovely_landscapes.txt c_memorable_moments.txt d_pet_pictures.txt e_shiny_selfies.txt
    public static void main(String[] args) throws IOException {

        for(int i=0; i< args.length; ++i) {
            result = new ArrayList<>();
            tags = new HashMap<>();

            Photo[] photosArr = readFile(args[i]);

            Map<Integer, Photo> photos = new HashMap<>();
            for (Photo photo : photosArr) {
                photos.put(photo.number, photo);
            }

            solve(photos);

            writeResult(result, args[i].replace(".txt", ".out"));
        }
    }

    private static void solve(Map<Integer, Photo> photos) {

        Photo curPhoto = null;
        Set<String> curTags;
        for(int i=0; i<photos.size(); ++i) {
            if(!photos.get(i).isVert) {
                curPhoto = photos.get(i);
                break;
            }
        }
        if(curPhoto == null) {
            result.add(new int[]{0, 1});
            curTags = new HashSet<>(photos.get(0).tags);
            curTags.addAll(photos.get(1).tags);
            curPhoto = photos.remove(1);
            photos.remove(0);

        } else {
            photos.remove(curPhoto.number);
            curTags = new HashSet<>(curPhoto.tags);
            result.add(new int[]{curPhoto.number});
        }

        while (!photos.isEmpty()) {
            if(photos.size() % 100 == 0)
                System.out.println(photos.size());

            int maxCommonNumber = getMaxCommonPhotoNumber(photos, curPhoto, curTags, false);

            Photo founded = photos.get(maxCommonNumber);
            if(founded == null)
                return;

            if(founded.isVert) {
                int f2 = getMaxCommonPhotoNumber(photos, founded, curTags, true);
                Photo founded2 = photos.get(f2);
                if(founded2 == null || !founded2.isVert)
                    return;
                if(founded.number != founded2.number)
                    result.add(new int[] {founded.number, founded2.number});

                curTags = new HashSet<>(founded.tags);
                curTags.addAll(founded2.tags);

                photos.remove(founded.number);
                photos.remove(founded2.number);
            } else {
                result.add(new int[] {founded.number});
                curTags = new HashSet<>(founded.tags);

                photos.remove(founded.number);
            }
        }
    }

    private static int getMaxCommonPhotoNumber(Map<Integer, Photo> photos, Photo curPhoto, Set<String> curTags, boolean onlyVert) {
        int maxCommon = -1;
        int maxCommonNumber = -1;

        for(String tt : curTags) {

            List<Integer> photos1 = tags.get(tt);

            for (Integer num : photos1) {

                Photo photoNext = photos.get(num);
                if(photoNext == null)
                    continue;

                if (onlyVert && !photoNext.isVert)
                    continue;

                if (photoNext.number == curPhoto.number)
                    continue;

                int common = 0;
                for (String t : curTags) {
                    if (photoNext.tags.contains(t))
                        ++common;
                }

                if (common > maxCommon && Math.min(curPhoto.tagCount - maxCommon, photoNext.tagCount - maxCommon) >= common) {
                    maxCommon = common;
                    maxCommonNumber = num;
                    if (maxCommon > (curPhoto.tagCount / 2) - 1)
                        return maxCommonNumber;
                }
            }
        }
        if(maxCommonNumber > -1)
            return maxCommonNumber;
        else {
            if(!onlyVert)
                return photos.keySet().iterator().next();
            else {
                Iterator<Integer> it = photos.keySet().iterator();
                Integer val = it.next();
                while (it.hasNext() && !photos.get(val).isVert) {
                    val = it.next();
                }
                return val;
            }
        }
    }

    private static void writeResult(List<int[]> result, String file) {

        List<String> toWrite = new ArrayList<>();
        toWrite.add(String.valueOf(result.size()));

        for(int i=0; i< result.size(); ++i) {
            int[] c = result.get(i);

            if(c.length == 1) {
                toWrite.add(String.valueOf(c[0]));
            } else {
                toWrite.add(String.valueOf(c[0]) + " " + String.valueOf(c[1]));
            }
        }

        try {
            Files.write(Paths.get(file), toWrite);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static Photo[] readFile(String filePath) throws IOException {

        List<String> lines = Files.readAllLines(Paths.get(filePath));

        int count = Integer.parseInt(lines.get(0));

        Photo[] data = new Photo[count];

        for(int i=1; i<lines.size(); ++i) {
            String[] parts = lines.get(i).split(" ");
            int tCount = Integer.parseInt(parts[1]);
            boolean isVerts = parts[0].equals("V");
            Photo cur = new Photo(i - 1, tCount, new HashSet<>(), isVerts);

            for (int j = 0; j < tCount; ++j) {
                cur.tags.add(parts[j + 2]);
                tags.computeIfAbsent(parts[j + 2], p -> new ArrayList<>());
                tags.get(parts[j + 2]).add(cur.number);
            }
            data[i - 1] = cur;
        }

        Arrays.sort(data, (p1,p2) -> {

            if(p1.isVert != p2.isVert)
                return p1.isVert?1:-1;

            if(p1.tagCount != p2.tagCount)
                return p1.tagCount - p2.tagCount;
            return 0;
        });

        return data;
    }
}

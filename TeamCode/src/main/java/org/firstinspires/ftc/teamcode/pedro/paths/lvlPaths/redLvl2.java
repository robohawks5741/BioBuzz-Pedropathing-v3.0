package org.firstinspires.ftc.teamcode.pedro.paths.lvlPaths;

import static com.pedropathing.api.Paths.*;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

public class redLvl2 {

    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(60, 8, 90);
    private final Pose path1 = poseFactory.of(60, 24, 90);
    private final Pose point2 = poseFactory.of(16.4255, 110.7406, -178.463);
    private final Pose point2Control1 = poseFactory.of(56.1566, 103.8085, 0);
    private final Pose point2Control2 = poseFactory.of(70.3651, 112.2717, 0);

    public Path path1() {
        return line(start, path1).constant(path1);
    }

    public Path path2() {
        return curve(path1, point2Control1, point2Control2, point2).tangent();
    }
}
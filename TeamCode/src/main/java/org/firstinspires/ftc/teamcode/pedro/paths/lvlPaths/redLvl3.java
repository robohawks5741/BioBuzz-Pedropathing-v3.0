package org.firstinspires.ftc.teamcode.pedro.paths.lvlPaths;

import static com.pedropathing.api.Paths.*;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.interpolator.Interpolator;

public class redLvl3 {

    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(9, 133, 90);
    private final Pose path1 = poseFactory.of(60, 36, 90);
    private final Pose path1Control1 = poseFactory.of(75.3585, 105.2226, 0);
    private final Pose path1Control2 = poseFactory.of(58.3538, 97.3142, 0);
    private final Pose path1Segment1Start = poseFactory.of(60, 36, 0);
    private final Pose path1Segment1End = poseFactory.of(60, 36, 90);
    private final Pose path1Segment2Heading = poseFactory.of(60, 36, 90);
    private final Pose point2 = poseFactory.of(13.5453, 113.0896, 173.8224);
    private final Pose point2Control1 = poseFactory.of(60.4774, 125.4991, 0);
    private final Pose point2Control2 = poseFactory.of(58.8877, 108.0075, 0);

    public Path path1() {
        return curve(start, path1Control1, path1Control2, path1).heading(Interpolator.piecewise().until(0.4313, Interpolator.linear(path1Segment1Start, path1Segment1End)).until(1, Interpolator.constant(path1Segment2Heading)));
    }

    public Path path2() {
        return curve(path1, point2Control1, point2Control2, point2).tangent();
    }
}
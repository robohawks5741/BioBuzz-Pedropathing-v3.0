package org.firstinspires.ftc.teamcode.pedro.paths.lvlPaths;

import static com.pedropathing.ivy.pedro.PedroCommands.follow;
import static org.firstinspires.ftc.teamcode.pedro.Constants.RobotConstants.poseFactory;

import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.pedro.Constants;

@Autonomous
class redLvl1PathTest extends LinearOpMode {
    Follower follower;

    boolean isRedAlliance = true;

    @Override
    public void runOpMode() throws InterruptedException {
       
            redLvl1 paths = new redLvl1();

        follower = Constants.create(hardwareMap);

        follower.setPose(paths.start);
        follower.update();
        Scheduler.reset();
        Command RunPath1 = follow(follower, paths.path1());

        waitForStart();
        Scheduler.schedule(RunPath1);

        while (opModeIsActive()) {
            follower.update();
            Scheduler.execute();

            telemetry.addData("X", follower.pose().x());
            telemetry.addData("Y", follower.pose().y());
            telemetry.addData("Heading", Math.toDegrees(follower.pose().heading()));
            telemetry.addData("Follower Mode", follower.mode());
            telemetry.update();
        }
    }
}

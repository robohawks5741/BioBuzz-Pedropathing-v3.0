package org.firstinspires.ftc.teamcode.pedro.testOpModes;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.pedro.Constants;

public class fieldOrientedTest extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        Follower follower = Constants.create(hardwareMap);
        waitForStart();
        while (opModeIsActive()) {
            DrivePowers powers = ManualDrive.fieldCentric(
                    -gamepad1.left_stick_y,
                    gamepad1.left_stick_x,
                    gamepad1.right_stick_x,
                    follower.pose().heading()
            );

            follower.manual(powers);
            follower.update();
            Pose robotPose = follower.pose(); // returns a Pose object

            telemetry.addData("Robot X", robotPose.x());
            telemetry.addData("Robot Y", robotPose.y());
            telemetry.addData("Robot Heading", Math.toDegrees(robotPose.heading()));
        }
    }
}

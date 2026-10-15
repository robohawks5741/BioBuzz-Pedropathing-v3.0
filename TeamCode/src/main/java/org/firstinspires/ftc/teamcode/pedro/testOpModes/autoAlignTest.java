package org.firstinspires.ftc.teamcode.pedro.testOpModes;

import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.pedro.paths.redPaths;
@TeleOp(name="autoAlignTest")
public class autoAlignTest extends LinearOpMode {
    redPaths paths = new redPaths();
    Follower follower;
    @Override
    public void runOpMode() throws InterruptedException {
        follower = Constants.create(hardwareMap);
        follower.setPose(paths.start);
        boolean aPressedLastCycle = false;
        boolean inAuto = false;
        boolean tempOverride = false;
        Path currentPath = null;

        waitForStart();
        while (opModeIsActive()) {
            follower.update();
            if (gamepad1.a && !aPressedLastCycle) {
                aPressedLastCycle = true;
                inAuto = !inAuto;
                currentPath = curve(follower.pose(), Constants.RobotConstants.redNorthCell).linear(follower.pose().heading(), Constants.RobotConstants.redNorthCell.heading());
                Scheduler.schedule(follow(follower, currentPath));
            } else if(!gamepad1.a){
                aPressedLastCycle = false;
            }
            tempOverride = (gamepad1.left_stick_x != 0 || gamepad1.left_stick_y != 0 || gamepad1.right_stick_x != 0);
            currentPath = tempOverride ? curve(follower.pose(), Constants.RobotConstants.redNorthCell).linear(follower.pose().heading(), Constants.RobotConstants.redNorthCell.heading()) : currentPath;
            if (inAuto && !tempOverride) {

                Scheduler.execute();

            } else {
                DrivePowers powers = ManualDrive.fieldCentric(
                        gamepad1.left_stick_x,
                        -gamepad1.left_stick_y,
                        -gamepad1.right_stick_x,
                        follower.pose().heading()
                );
                follower.manual(powers);
            }

        }
    }
}

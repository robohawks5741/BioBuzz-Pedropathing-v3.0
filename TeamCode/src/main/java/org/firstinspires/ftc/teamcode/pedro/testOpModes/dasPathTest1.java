package org.firstinspires.ftc.teamcode.pedro.testOpModes;


import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.localization.Localizer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.pedropathing.ivy.Scheduler;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.pedro.Constants;

import static com.pedropathing.ivy.Scheduler.execute;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.commands.Commands.*;
import static com.pedropathing.ivy.groups.Groups.*;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;
import static org.firstinspires.ftc.teamcode.pedro.paths.redPaths.*;

import org.firstinspires.ftc.teamcode.pedro.paths.redPaths;

@Autonomous
public class dasPathTest1 extends LinearOpMode {
    redPaths paths = new redPaths();
    Follower follower;
    @Override
    public void runOpMode() {

        follower = Constants.create(hardwareMap);
        follower.setPose(paths.start);
        follower.update();
        //Since the scheduler is static, we need to reset it before each OpMode
        //so commands don't carry over from one OpMode to the next
        Scheduler.reset();
        Command runPath1 = follow(follower, paths.rpath1());
        Command runPath2 = follow(follower, paths.rpath2());
        Command runPath3 = follow(follower, paths.rpath3());
        Command runPath4 = follow(follower, paths.rpath4());
        Command wait = waitMs(1000);
        Command sequence = sequential(
                runPath1,
                wait,
                runPath2,
                runPath3,
                wait,
                runPath4
        );

        waitForStart();

        // Schedule the sequence when the OpMode starts
        schedule(sequence);

        while (opModeIsActive()) {
            // Run the scheduler each loop
            follower.update();
            Scheduler.execute();
            // add your other methods needed in the loop here

            telemetry.addData("X", follower.pose().x());
            telemetry.addData("Y", follower.pose().y());
            telemetry.addData("Heading", Math.toDegrees(follower.pose().heading()));
            telemetry.addData("Follower Mode", follower.mode());
            telemetry.update();
        }
    }
}

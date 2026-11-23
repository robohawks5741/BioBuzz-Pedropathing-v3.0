package org.firstinspires.ftc.teamcode.pedro.testOpModes;
import static org.firstinspires.ftc.teamcode.pedro.Constants.RobotConstants.launchRPM;

import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.pedro.subsystemControl;

@TeleOp(name = "Flywheel Test")
public class flywheelTest extends LinearOpMode {

    subsystemControl subsystemController = new subsystemControl();
    DcMotorEx launcher;
    DcMotorEx launcher2;
    Command spinTo;
    //Command spinTo2;
    @Override
    public void runOpMode() throws InterruptedException {
        Follower follower = Constants.create(hardwareMap);

        boolean launchPressedLastCycle = false;
        boolean launchCycleRunning = false;
        double targetRPM = 0;
        subsystemController.initializeSubsystems(this);
        waitForStart();
        while (opModeIsActive()) {

            //manual launcher control
            if (gamepad1.y){
                subsystemController.targetRPM = launchRPM;
            } else if (!subsystemController.launchCycle().isScheduled()) {
                subsystemController.targetRPM = 0;
               // spinTo = launchControl.spinTo(0, launcher, 200);

                //spinTo.execute();
            }



            //this exists to help me figure out where the correct position values
            //for the launch door servo are
            if (gamepad1.dpadLeftWasPressed()) {
                subsystemController.launchDoorOpen += 0.01;
            } else if (gamepad1.dpadRightWasPressed()) {
                subsystemController.launchDoorOpen -= 0.01;
            } else if (gamepad1.dpadUpWasPressed()) {
                subsystemController.launchDoorClosed += 0.01;
            } else if (gamepad1.dpadDownWasPressed()) {
                subsystemController.launchDoorClosed -= 0.01;
            }

            //manual launcher door control
            if (gamepad1.leftBumperWasPressed()) {
                subsystemController.launchDoor.setPosition(subsystemController.launchDoorOpen);
            } else if (gamepad1.leftTriggerWasPressed()) {
                subsystemController.launchDoor.setPosition(subsystemController.launchDoorClosed);
            }

            //manual feed control
            subsystemController.submitSubSysRequest(gamepad1.right_trigger-gamepad1.left_trigger, Constants.feedPriorities.driver, 0);


            //toggles launch cycle
            if (gamepad1.aWasPressed()) {
                if (!subsystemController.launchCycle().isScheduled()){
                    subsystemController.launchCycle().schedule();
                } else {
                    subsystemController.launchCycle().cancel();
                }
            }


            double forward = -gamepad1.left_stick_y;
            double lateral = -gamepad1.left_stick_x;
            double turn = -gamepad1.right_stick_x;

            follower.manual(forward, lateral, turn);
            follower.update();
            telemetry.addData("Power (1)",subsystemController.launchMotor.getPower());
            telemetry.addData("Power (2)", subsystemController.launchMotor2.getPower());
            telemetry.addData("launchCycle.isScheduled", subsystemController.launchCycle().isScheduled());
            telemetry.addData("Target RPM", subsystemController.targetRPM);
            telemetry.addData("launchDoorOpen", subsystemController.launchDoorOpen);
            telemetry.addData("launchDoorClosed", subsystemController.launchDoorClosed);
            telemetry.addData("launchDoorPosition", subsystemController.launchDoor.getPosition());
            telemetry.addData("cycleStarted", subsystemController.cycleStarted);

            telemetry.addData("Current RPM (1)", subsystemController.launchMotor.getVelocity()*(60.0/28.0));
            telemetry.addData("Current RPM (2)", subsystemController.launchMotor2.getVelocity()*(60.0/28.0));
            telemetry.addData("launchPressedLastCycle", launchPressedLastCycle);
            updateTelemetry(telemetry);
            subsystemController.updateAll();


            //subsystemController.updateFeed();

        }


    }
}

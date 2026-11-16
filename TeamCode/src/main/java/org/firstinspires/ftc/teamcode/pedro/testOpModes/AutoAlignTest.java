package org.firstinspires.ftc.teamcode.pedro.testOpModes;

import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.pedro.paths.redPaths;
@TeleOp(name="AutoAlignTest")
public class AutoAlignTest extends LinearOpMode {
    redPaths paths = new redPaths();
    Follower follower;
    boolean isRedSide = true;

    @Override
    public void runOpMode() throws InterruptedException {
        //create the follower and set the start pose
        follower = Constants.create(hardwareMap);
        follower.setPose(Constants.RobotConstants.start);
        VoltageSensor batteryVoltageSensor = hardwareMap.voltageSensor.iterator().next();

        //targetCell represents the pose of the launcher location for the cell that is decided on based on Alliance (isRedSide) and location (y > 72, aka North vs South)
        Pose targetCell;

        //pose of the cell to aim at in aim only mode
        Pose targetCellAim;

        boolean xPressedLastCycle = false;
        boolean bPressedLastCycle = false;
        boolean tempOverrideLastCycle = false;
        boolean inAuto = false;
        boolean autoNorth = false;
        boolean autoSouth = false;
        boolean tempOverride = false;
        Path currentPath = null;
        Path aimOnlyPath = null;


        waitForStart();
        while (opModeIsActive()) {
            follower.update(); //update follower every cycle

            //Figure out which launch area/cell to go to, depends on side (isRedSide) and if robot is on the "north" or "south" (away from audience and near audience respectively, y>72) of the field
            targetCell = follower.pose().y() > 72 ? isRedSide ? Constants.RobotConstants.redNorthCell : Constants.RobotConstants.blueNorthCell : isRedSide ? Constants.RobotConstants.redSouthCell : Constants.RobotConstants.blueSouthCell;
            targetCellAim = follower.pose().y() > 72 ? isRedSide ? Constants.RobotConstants.redNorthCellAim : Constants.RobotConstants.blueNorthCellAim : isRedSide ? Constants.RobotConstants.redSouthCellAim : Constants.RobotConstants.blueSouthCellAim;

            if (gamepad1.x && !xPressedLastCycle) { //Runs whenever auto is TOGGLED (aka a is being pressed but was not last cycle)
                inAuto = autoSouth? true: !inAuto; //toggle auto
                autoNorth = !autoNorth; //toggle autoNorth
                autoSouth = false;
                currentPath = line( //sets the current path as current location -> targetCell
                        follower.pose(),
                        targetCell).linear(follower.pose().heading(), targetCell.heading());
                Scheduler.schedule(follow(follower, currentPath)); /* Schedule the path to the scheduler (Do not do this every cycle
                only when path has changed due to driver input) */
            }
            if (gamepad1.b && !bPressedLastCycle) { //Runs whenever auto is TOGGLED (aka a is being pressed but was not last cycle)

                inAuto = autoNorth ? true : !inAuto; //toggle auto but if it was already in auto for the other side dont turn off
                autoSouth = !autoSouth; //toggle autoNorth
                autoNorth = false;
                currentPath = line( //sets the current path as current location -> targetCell
                        follower.pose(),
                        targetCell).linear(follower.pose().heading(), targetCell.heading());
                Scheduler.schedule(follow(follower, currentPath)); /* Schedule the path to the scheduler (Do not do this every cycle
                only when path has changed due to driver input) */
            }

            //tempOverride is true if there is any input on any of the sticks
            //tempOverride is used when the driver overrides the auto pathing while in auto mode temporarily
            tempOverride = (gamepad1.left_stick_x != 0 || gamepad1.left_stick_y != 0 || gamepad1.right_stick_x != 0 || gamepad1.right_bumper);


            //if in tempOverride, update the path to be from the new current position to targetCell, else do not change the path
            currentPath = tempOverride ? line(follower.pose(), targetCell).linear(follower.pose().heading(), targetCell.heading()) : currentPath;

            if (inAuto && !tempOverride) { //in auto and not in temporary driver override
                if (tempOverrideLastCycle) { //make sure there are no residual drive powers and scheduler is reset and updated with the current follow command

                    follower.manual(0, 0, 0);
                    Scheduler.reset();
                    Scheduler.schedule(follow(follower, currentPath));
                }
                Scheduler.execute(); //execute the scheduler--in this case, the follow command


            } else {
                //Manual driving (either not in auto or in auto but overridden with joystick input)
                DrivePowers powers = ManualDrive.fieldCentric(
                        -gamepad1.left_stick_y,
                        -gamepad1.left_stick_x,
                        -gamepad1.right_stick_x,
                        follower.pose().heading()
                );
                follower.manual(powers);
            }
            if (gamepad1.right_bumper) {
                aimOnlyPath = line(follower.pose(), new Pose(follower.pose().x(), follower.pose().y(), Math.atan2(targetCellAim.y()-follower.pose().y(), targetCellAim.x()-follower.pose().x())));
                follower.manual(0, 0, 0);
                Scheduler.reset();
                Scheduler.schedule(follow(follower, aimOnlyPath));
            }

            xPressedLastCycle=gamepad1.x;
            bPressedLastCycle=gamepad1.b;

            //This one's obvious (makes sure that tempOverrideLastCycle in the next cycle
            // is whatever tempOverride is this cycle)
            tempOverrideLastCycle = tempOverride;

            telemetry.addData("tempOverride", tempOverride);
            telemetry.addData("Powers (FLH)", gamepad1.left_stick_x + "\n" + -gamepad1.left_stick_y + "\n" + -gamepad1.right_stick_x);


            }
        }
    }

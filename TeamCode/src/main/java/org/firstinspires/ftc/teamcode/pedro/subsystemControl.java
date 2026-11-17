package org.firstinspires.ftc.teamcode.pedro;

import static com.pedropathing.ivy.commands.Commands.instant;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.pedro.Constants.feedPriorities;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import java.util.concurrent.TimeUnit;

public class subsystemControl extends OpMode{

    public DcMotorEx launchMotor;
    public DcMotorEx launchMotor2;
    public DcMotorSimple feed;
    public Servo launchDoor;
    public double rpm;
    double overspeedTolerance = 200;
    public boolean cycleStarted = false;
    public ElapsedTime elapsedTime = new ElapsedTime(ElapsedTime.Resolution.SECONDS);
    public VoltageSensor batteryVoltageSensor;
    public double launchRPM = Constants.RobotConstants.launchRPM;

    public double launchDoorClosed = 0.25;
    public double launchDoorOpen = 0.19;



    feedRequest currentFeedRequest;

    @Override
    public void init() {

    }

    @Override
    public void loop() {

    }

    /**
     * Class for feed request (constructor).
     * on: whether to run the feed or not.
     * priority: the priority of the feed request.
     */
    class feedRequest {
        boolean on;
        int priority;

    }

    /**
     * initializes all subsystems.
     * Must be called first to establish hardware map connections
     */
    public void initializeSubsystems(OpMode opMode) {

        //launch motors
        launchMotor = opMode.hardwareMap.get(DcMotorEx.class, "testMotor2");
        launchMotor2 = opMode.hardwareMap.get(DcMotorEx.class, "testMotor");
        launchMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        launchMotor2.setDirection(DcMotorSimple.Direction.REVERSE);

        //feed motor
        feed = opMode.hardwareMap.get(DcMotorSimple.class, "feed");
        feed.setDirection(DcMotorSimple.Direction.REVERSE);

        launchDoor = opMode.hardwareMap.get(Servo.class, "launchDoor");

        //battery voltage sensor
        batteryVoltageSensor = opMode.hardwareMap.voltageSensor.iterator().next();

    }

    /**
     * updates feed,
     * ensures launchRPM is up to date,
     * updates telemetry,
     * executes scheduler,
     */
    public void updateAll() {
        updateFeed();
        launchRPM = Constants.RobotConstants.launchRPM;
        telemetry.update();
        Scheduler.execute();
    }

    /**
     *Runs the automatic launch cycle.
     * Must be called every loop cycle, since it does not run its own loop.
     * @return Command that runs the launch cycle
     */
    public Command launchCycle() {

        double motorRPM = launchMotor.getVelocity()*(60.0/28.0);

        double launchTime = 4- Math.pow((-(batteryVoltageSensor.getVoltage()-13)/2), 1.8);
        launchTime = 4;

        //checks if still within time limit (launchTime)
        //if yes, spin to 3000 rpm and submit feed request to feed if up to speed
        //if no, set rpm to 0, clear currentFeedRequest so that driver has control again
        /*if (elapsedTime.time(TimeUnit.SECONDS) < launchTime) {
            rpm = 3000;
            spinTo().execute();
            return Math.abs(rpm - motorRPM) < 100 ?
                    Command.build().setExecute(() -> submitFeedRequest(true, feedPriorities.launchCycle)) :
                    Command.build().setExecute(() -> submitFeedRequest(false, feedPriorities.launchCycle));

        } else {
            rpm = 0;

            return Command.build()
                    .setDone(()-> true)
                    .setEnd((endCondition) -> {
                        elapsedTime.reset();
                        clearFeed();
                    });
        }*/

        double finalLaunchTime = launchTime;
        return Command.build()

                .setStart(()->{
                    elapsedTime.reset();

                })
                //Every cycle that it's running, spin to 3000 rpm and submit feed request to feed if up to speed
                .setExecute(() -> {
                    rpm = launchRPM;
                    spinTo.schedule();
                    if (Math.abs(rpm - motorRPM) < 100) {

                        launchDoor.setPosition(launchDoorOpen);
                        submitFeedRequest(true, feedPriorities.launchCycle);
                    } else {
                        launchDoor.setPosition(launchDoorClosed);
                        submitFeedRequest(false, feedPriorities.launchCycle);
                    }
                    //telemetry.addData("LaunchCycleRunning", true);


                })

                //Ends launch cycle when time is up
                .setDone(()-> elapsedTime.time(TimeUnit.SECONDS) > finalLaunchTime)

                //When ended, set rpm to 0, reset the timer, and clear currentFeedRequest so that driver has control again.
                .setEnd((endCondition) -> {
                    rpm = 0;
                    elapsedTime.reset();
                    launchDoor.setPosition(launchDoorClosed);
                    clearFeed();
                });
    }


    /**
     * Bang-bang controller, tries to spin the provided motor to provided rpm.
     * If motor rpm under rpm target, full positive power
     * If over rpm target + overspeedTolerance, full negative power
     * If within tolerance, medium power to keep it that speed
     */
    public Command spinTo = instant(()-> {
        double motorRPM = launchMotor.getVelocity()*(60.0/28.0);
        if (rpm+overspeedTolerance < motorRPM || rpm == 0){
            launchMotor.setPower(0);
        } else if (rpm > motorRPM && motorRPM >= 0) {
            launchMotor.setPower(1);
        }else if ((rpm-motorRPM)<200){
            launchMotor.setPower(rpm/4500);
        }else {
            launchMotor.setPower(0);
        }
        launchMotor2.setPower(launchMotor.getPower());
    });

    //greater priority means higher priority

    /**
     * manages feed priority
     * @param request is the incoming request
     * If currentFeedRequest is null, set currentFeedRequest to request.
     * Checks priority of currentFeedRequest and request, whichever higher becomes currentFeedRequest.
     */
    public void feedPriorityManager(feedRequest request) {

        if (currentFeedRequest == null) {
            currentFeedRequest = request;
        } else if (currentFeedRequest.priority <= request.priority) {
            currentFeedRequest = request;
        }
        if (!launchCycle().isScheduled() && request.priority == 0) {
            currentFeedRequest = request;
        }
    }

    /**
     * Submits a new feed request from scratch, and sends it through the feed priority manager
     * @param on whether to run the feed or not
     * @param priority the priority of the feed request
     */
    public void submitFeedRequest(boolean on, int priority) {
        feedRequest request = new feedRequest();
        request.on = on;
        request.priority = priority;
        feedPriorityManager(request);
    }

    /**
     * Updates the feed based on the current feed request
     */
    public void updateFeed() {
        if (currentFeedRequest != null) {
            feed(currentFeedRequest.on);
        } else {
            feed(false);
        }
    }

    /**
     * Clears the current feed request by setting currentFeedRequest to null
     */
    public void clearFeed(){
        currentFeedRequest = null;
    }

    /**
     * Sets the power of the feed motor
     * @param on whether to run the feed or not
     */
    public void feed(boolean on) {
        feed.setPower(on? 1 : 0);
    }
}

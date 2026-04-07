document.addEventListener('DOMContentLoaded', function() {
    //form elements
    const sex = document.getElementById('sex');
    const dateOfBirth = document.getElementById('dateOfBirth');
    const height = document.getElementById('height');
    const weight = document.getElementById('weight');
    const weeklyWorkouts = document.getElementById('weeklyWorkoutGoalCount');
    const fitnessGoal = document.getElementById('fitnessGoal');
    const preview = document.getElementById('nutritionPreview');
    
    //preview display elements
    const previewDailyCalories = document.getElementById('previewDailyCalories');
    const previewWeeklyCalories = document.getElementById('previewWeeklyCalories');
    const previewWeeklyBurnTarget = document.getElementById('previewWeeklyBurnTarget'); 
    const previewProtein = document.getElementById('previewProtein');
    const previewCarbs = document.getElementById('previewCarbs');
    const previewFats = document.getElementById('previewFats');
    const previewFibre = document.getElementById('previewFibre');
    
    // hidden input fields
    const hiddenWeeklyConsumed = document.getElementById('weeklyCaloriesConsumedTarget');
    const hiddenWeeklyBurned = document.getElementById('weeklyCaloriesBurnedTarget');
    const hiddenProtein = document.getElementById('dailyProtienTarget');
    const hiddenCarbs = document.getElementById('dailyCarbsTarget');
    const hiddenFats = document.getElementById('dailyFatsTarget');
    const hiddenFibre = document.getElementById('dailyFibreTarget');
    
    function calculateAge(birthDate) {
        if (!birthDate) return 30;
        const today = new Date();
        let age = today.getFullYear() - birthDate.getFullYear();
        const m = today.getMonth() - birthDate.getMonth();
        if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
            age--;
        }
        return age;
    }
    
    function getActivityMultiplier(workouts) {
        if (workouts <= 1) return 1.2;
        if (workouts <= 3) return 1.375;
        if (workouts <= 5) return 1.55;
        if (workouts <= 6) return 1.725;
        return 1.9;
    }
    
    function calculateBMR() {
        const weightKg = parseFloat(weight?.value) || 70;
        const heightCm = parseFloat(height?.value) || 170;
        const birthDate = dateOfBirth?.value ? new Date(dateOfBirth.value) : null;
        const age = calculateAge(birthDate);
        const isMale = sex?.value === 'Male';
        
        if (isMale) {
            return (10 * weightKg) + (6.25 * heightCm) - (5 * age) + 5;
        } else {
            return (10 * weightKg) + (6.25 * heightCm) - (5 * age) - 161;
        }
    }
    
    function getGoalValues(goalName) {
        const goals = {
            'INTENSIVE_WEIGHT_LOSS': { 
                adjustment: -0.30,
                protein: 2.4,
                fat: 0.20,
                burnMultiplier: 12,      
                fibrePer1000Cal: 16
            },
            'MODERATE_WEIGHT_LOSS': { 
                adjustment: -0.20,
                protein: 2.0,
                fat: 0.25,
                burnMultiplier: 10,
                fibrePer1000Cal: 15
            },
            'MILD_WEIGHT_LOSS': { 
                adjustment: -0.10,
                protein: 1.8,
                fat: 0.25,
                burnMultiplier: 8,
                fibrePer1000Cal: 14
            },
            'MAINTAIN_WEIGHT': { 
                adjustment: 0.00,
                protein: 1.8,
                fat: 0.30,
                burnMultiplier: 7,
                fibrePer1000Cal: 14
            },
            'MILD_WEIGHT_GAIN': { 
                adjustment: 0.10,
                protein: 1.8,
                fat: 0.25,
                burnMultiplier: 6,
                fibrePer1000Cal: 13
            },
            'MODERATE_WEIGHT_GAIN': { 
                adjustment: 0.20,
                protein: 2.0,
                fat: 0.25,
                burnMultiplier: 5,
                fibrePer1000Cal: 12
            },
            'INTENSIVE_WEIGHT_GAIN': { 
                adjustment: 0.30,
                protein: 2.2,
                fat: 0.20,
                burnMultiplier: 4,
                fibrePer1000Cal: 11
            }
        };
        return goals[goalName] || goals['MAINTAIN_WEIGHT'];
    }
    
    function calculateFiber(targetCalories, goalVals) {
        let fibreGrams = (targetCalories / 1000) * goalVals.fibrePer1000Cal;
        fibreGrams = Math.min(50, Math.max(20, Math.round(fibreGrams)));
        return fibreGrams;
    }
    
    function updatePreview() {
        if (!fitnessGoal?.value || !weeklyWorkouts?.value) {
            if (preview) preview.style.display = 'none';
            return;
        }
        
        const workouts = parseInt(weeklyWorkouts.value) || 4;
        const weightKg = parseFloat(weight?.value) || 70;
        
        const bmr = calculateBMR();
        const tdee = bmr * getActivityMultiplier(workouts);
        const goalVals = getGoalValues(fitnessGoal.value);
        
        // Calc nutrition
        const targetCalories = Math.round(tdee * (1 + goalVals.adjustment));
        const weeklyCalories = targetCalories * 7;
        
        const proteinGrams = Math.round(weightKg * goalVals.protein);
        const proteinCalories = proteinGrams * 4;
        
        const fatCalories = targetCalories * goalVals.fat;
        const fatGrams = Math.round(fatCalories / 9);
        
        const carbCalories = targetCalories - proteinCalories - fatCalories;
        const carbGrams = Math.round(carbCalories / 4);
        
        const fibreGrams = calculateFiber(targetCalories, goalVals);
        
        // Calc weekly burn target
        const caloriesPerWorkout = weightKg * goalVals.burnMultiplier;
        const weeklyBurnTarget = Math.round(caloriesPerWorkout * workouts);
        
        // Update preview display
        if (previewDailyCalories) previewDailyCalories.textContent = targetCalories.toLocaleString();
        if (previewWeeklyCalories) previewWeeklyCalories.textContent = weeklyCalories.toLocaleString();
        if (previewWeeklyBurnTarget) previewWeeklyBurnTarget.textContent = weeklyBurnTarget.toLocaleString();  
        if (previewProtein) previewProtein.textContent = proteinGrams;
        if (previewCarbs) previewCarbs.textContent = carbGrams;
        if (previewFats) previewFats.textContent = fatGrams;
        if (previewFibre) previewFibre.textContent = fibreGrams;
        
        // Update hidden form fields
        if (hiddenWeeklyConsumed) hiddenWeeklyConsumed.value = weeklyCalories;
        if (hiddenWeeklyBurned) hiddenWeeklyBurned.value = weeklyBurnTarget;  
        if (hiddenProtein) hiddenProtein.value = proteinGrams;
        if (hiddenCarbs) hiddenCarbs.value = carbGrams;
        if (hiddenFats) hiddenFats.value = fatGrams;
        if (hiddenFibre) hiddenFibre.value = fibreGrams;
        
        if (preview) preview.style.display = 'block';
    }
    
    const inputs = [sex, dateOfBirth, height, weight, weeklyWorkouts, fitnessGoal];
    inputs.forEach(input => {
        if (input) {
            input.addEventListener('change', updatePreview);
            input.addEventListener('input', updatePreview);
        }
    });
    
    updatePreview();
});
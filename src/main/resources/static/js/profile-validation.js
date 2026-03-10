const profileForm = document.getElementById("profileForm");

profileForm.addEventListener("submit", function (event) {
  let isValid = true;

  const firstname = document.getElementById("firstname");
  const lastname = document.getElementById("lastname");
  const sex = document.getElementById("sex");
  const dateOfBirth = document.getElementById("dateOfBirth");
  const height = document.getElementById("height");
  const weight = document.getElementById("weight");
  const caloriesDailyGoal = document.getElementById("caloriesDailyGoal");

  clearValidation(firstname);
  clearValidation(lastname);
  clearValidation(sex);
  clearValidation(dateOfBirth);
  clearValidation(height);
  clearValidation(weight);
  clearValidation(caloriesDailyGoal);

  if (firstname.value.trim() === "") {
    setInvalid(firstname);
    isValid = false;
  }

  if (lastname.value.trim() === "") {
    setInvalid(lastname);
    isValid = false;
  }

  if (sex.value.trim() === "") {
    setInvalid(sex);
    isValid = false;
  }

  if (dateOfBirth.value.trim() === "") {
    setInvalid(dateOfBirth);
    isValid = false;
  } else {
    const dob = new Date(dateOfBirth.value);
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    if (dob >= today) {
      setInvalid(dateOfBirth);
      isValid = false;
    }
  }

  const heightValue = parseFloat(height.value);
  if (
    height.value.trim() === "" ||
    isNaN(heightValue) ||
    heightValue < 50 ||
    heightValue > 300
  ) {
    setInvalid(height);
    isValid = false;
  }

  const weightValue = parseFloat(weight.value);
  if (
    weight.value.trim() === "" ||
    isNaN(weightValue) ||
    weightValue < 20 ||
    weightValue > 500
  ) {
    setInvalid(weight);
    isValid = false;
  }

  const goalValue = parseInt(caloriesDailyGoal.value);
  if (
    caloriesDailyGoal.value.trim() === "" ||
    isNaN(goalValue) ||
    goalValue < 500 ||
    goalValue > 10000
  ) {
    setInvalid(caloriesDailyGoal);
    isValid = false;
  }

  if (!isValid) {
    event.preventDefault();
  }
});

function setInvalid(element) {
  element.classList.add("is-invalid");
}

function clearValidation(element) {
  element.classList.remove("is-invalid");
}

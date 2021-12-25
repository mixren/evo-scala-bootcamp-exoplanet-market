package io.github.mixren.evoscalabootcampexoplanetmarket.fakes

import io.github.mixren.evoscalabootcampexoplanetmarket.user.domain.{PasswordHash, User, UserName}

object FakeUsers {
  val validUsername1: UserName = UserName("Jax")
  val validUsername2: UserName = UserName("Allah")
  val validUsername3: UserName = UserName("Jin")
  val validUsername4: UserName = UserName("Vasya")

  val validPasswordHash1: PasswordHash = PasswordHash("8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92")
  val validPasswordHash2: PasswordHash = PasswordHash("7c814943b254da2d533159a2cb682c4a4a89c04babe76d0d9ca17a5fe9973340")

  val fakeUser1: User = User(validUsername1, validPasswordHash1)
  val fakeUser2: User = User(validUsername2, validPasswordHash2)
  val fakeUser3: User = User(validUsername3, validPasswordHash1)
  val fakeUser4: User = User(validUsername4, validPasswordHash2)

}

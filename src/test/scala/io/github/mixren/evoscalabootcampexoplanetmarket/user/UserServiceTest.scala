package io.github.mixren.evoscalabootcampexoplanetmarket.user

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.implicits.catsSyntaxEitherId
import io.github.mixren.evoscalabootcampexoplanetmarket.fakes.FakeAuthRequest._
import io.github.mixren.evoscalabootcampexoplanetmarket.user.domain.{AuthRequest, PasswordHash, User}
import io.github.mixren.evoscalabootcampexoplanetmarket.utils.HashGenerator
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec

class UserServiceTest extends AnyFlatSpec with MockFactory{

  val userRepositoryStub: UserRepositoryT[IO] = stub[UserRepositoryT[IO]]
  val userService = new UserService[IO](userRepositoryStub)

  val authRequest1: AuthRequest = fakeAuthRequest1
  val authRequest2: AuthRequest = fakeAuthRequest2
  val passHash1: PasswordHash = PasswordHash(HashGenerator.run(authRequest1.password.value))
  val passHash2: PasswordHash = PasswordHash(HashGenerator.run(authRequest2.password.value))
  val user1: User = authRequest1.asUser(passHash1)
  val user2: User = authRequest2.asUser(passHash2)

  "UserService" should "register users if they are not registered yet and return an authentication token" in{
    userRepositoryStub.createUser _ when (user1, *) returns IO.pure(0.asRight)
    userRepositoryStub.createUser _ when (user2, *) returns IO.pure("already in db".asLeft)
    assert(userService.userRegister(authRequest1).unsafeRunSync().isRight)
    assert(userService.userRegister(authRequest2).unsafeRunSync().isLeft)
  }

  it should "login registered users and return an authentication token" in{
    userRepositoryStub.userByName _ when authRequest1.username returns IO.pure(Some(user1))
    userRepositoryStub.userByName _ when authRequest2.username returns IO.pure(None)
    assert(userService.userLogin(authRequest1).unsafeRunSync().isRight)
    assert(userService.userLogin(authRequest2).unsafeRunSync().isLeft)
  }
}

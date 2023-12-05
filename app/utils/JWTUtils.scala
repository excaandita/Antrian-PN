package utils
//
//@javax.inject.Singleton
//class JWTUtils @Inject()(userData: UserData){
//	implicit val clock: Clock = Clock.systemUTC
//	implicit val conf:Configuration = Configuration.reference
//
//	def createToken(user: User): String = {
//		val session = JwtSession() ++ (("user" , user), ("createTime", (new Date().getTime()/1000).toInt))
//		session.serialize
//	}
//
//	def readToken(token: String): (Option[User], Option[Int]) = {
//		val session = JwtSession.deserialize(token)
//		(session.getAs[User]("user"), session.getAs[Int]("createTime"))
//	}
//

	// End of Class
//}
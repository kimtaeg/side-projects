using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class Coin : MonoBehaviour
{
    private float minY = -7f;
    void Start()
    {
        Jump();
    }
    void Jump()
    {
        Rigidbody2D rigidBody = GetComponent<Rigidbody2D>();

        float randomJumpForce = Random.Range(4f, 8f);
        Vector2 jumpVelocity = Vector2.up * randomJumpForce;

        // 왼쪽 오른쪽 랜덤
        jumpVelocity.x = Random.Range(-2f, 2f);

        rigidBody.AddForce(jumpVelocity, ForceMode2D.Impulse);
    }

    void Update()
    {
        if (transform.position.y < minY)
        {
            Destroy(gameObject);
        }
    }
}

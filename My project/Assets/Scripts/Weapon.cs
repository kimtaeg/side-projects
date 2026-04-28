using UnityEngine;
using System.Collections;
using System.Collections.Generic;


public class Weapon : MonoBehaviour
{
    //유니티에서는 값을 변경할 수 있게 해준다.
    [SerializeField]

    private float moveSpeed = 10f;
    public float damage = 1f;

    //오브젝트를 생성되면 1초후에 없애고싶을때 메모리 많이 잡아먹어 없애는거임
    void Start()
    {
        Destroy(gameObject, 1f);
    }

    void Update()
    {
        transform.position += Vector3.up * moveSpeed * Time.deltaTime;
    }
}

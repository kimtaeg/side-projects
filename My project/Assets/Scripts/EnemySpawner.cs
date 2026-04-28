using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class EnemySpawner : MonoBehaviour
{
    [SerializeField]
    private GameObject[] enemies;

    [SerializeField]
    private GameObject boss;
    private float[] arrPosX = {-2.2f, -1.1f, 0f, 1.1f, 2.2f};

    [SerializeField]
    private float spawnInterval = 1.5f;

    void Start()
    {
        StratEnemyRoutine();
    }

    //메소드 코루틴 원하는만큼 어떤 시간을 정의해 줄 수 있다
    void StratEnemyRoutine()
    {
        StartCoroutine("EnemyRoutine");
    }

    public void StopEnemyRoutine()
    {
        StopCoroutine("EnemyRoutine");
    }

    IEnumerator EnemyRoutine()
    {
        yield return new WaitForSeconds(3f);

        float moveSpead = 5f;
        int spawnCount = 0;
        int enemyIndex = 0;

        while (true){
            foreach(float posX in arrPosX)
            {
                int index = Random.Range(0,enemies.Length);
                SpawnEmemy(posX, enemyIndex, moveSpead);
            }


            // 시간이 지나면 enemy인덱스가 올라간다.
            spawnCount++;

            if (spawnCount % 10 == 0)
            {// 10, 20, 30 ...
                enemyIndex += 1;
                moveSpead += 2;
            }

            if(enemyIndex >= enemies.Length)
            {
                SpawnBoss();
                enemyIndex = 0;
                moveSpead = 5f;
            }

            yield return new WaitForSeconds(spawnInterval);
        }
    }

    void SpawnEmemy(float posX, int index, float moveSpead)
    {
        Vector3 spawnPos = new Vector3(posX, transform.position.y, transform.position.z);

        // enemy에서 랜덤으로 단계별로 섞여나오는거
        if (Random.Range(0, 5) == 0)
        {
            index += 1;
        }

        if (index >= enemies.Length)
        {
            index = enemies.Length - 1;
        }
        GameObject enemyObject = Instantiate(enemies[index], spawnPos, Quaternion.identity);
        Enemy enemy = enemyObject.GetComponent<Enemy>();
        enemy.SetMoveSpeed(moveSpead);
    }

    void SpawnBoss()
    {
        Instantiate(boss, transform.position, Quaternion.identity);
    }
}
